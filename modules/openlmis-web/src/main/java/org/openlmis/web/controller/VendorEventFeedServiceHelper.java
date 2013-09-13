/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openlmis.core.exception.DataException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class VendorEventFeedServiceHelper {

  private static final Pattern XML_CDATA_PATTERN = compile("(?:<!\\[CDATA\\[)(.+)(?:\\]\\]>)");
  private static final String VENDOR_MAPPING_TEMPLATE = "vendorMapping_%s_%s.xml";

  private static final Logger logger = Logger.getLogger(VendorEventFeedServiceHelper.class);

  public static String getRecentFeed(EventFeedService eventFeedService, String requestURL, String vendor, String category) {
    try {
      Feed feed = eventFeedService.getRecentFeed(new URI(requestURL), category);
      mapFeedBasedOnVendorAndCategory(feed, vendor, category);
      return new WireFeedOutput().outputString(feed);
    } catch (Exception e) {
      logger.error("error occurred while getting recent feeds", e);
      throw new RuntimeException("Unexpected error", e); //TODO
    }
  }

  static File vendorMappingTemplate(String templateName) throws IOException {
    try {
      return new ClassPathResource(templateName).getFile();
    } catch (FileNotFoundException fne) {
      return null;
    }
  }

  private static void mapFeedBasedOnVendorAndCategory(Feed feed, String vendor,
                                                      String category) throws IOException {
    String templateName = format(VENDOR_MAPPING_TEMPLATE, vendor, category);

    File templateFile;
    if (isEmpty(vendor) || (templateFile = vendorMappingTemplate(templateName)) == null) {
      return;
    }

    Map<String, String> map = createTemplateMap(templateFile);

    List<Entry> feedEntries = feed.getEntries();
    for (Entry entry : feedEntries) {
      List<Content> contentList = entry.getContents();
      for (Content content : contentList) {
        JsonNode rootNode = convertToTemplate(map, parseAtomFeedContent(content.getValue()));
        content.setValue("<![CDATA[" + rootNode.toString() + "]]>");
      }
    }
  }

  static String parseAtomFeedContent(String atomFeedContentValue) {
    Matcher matcher = XML_CDATA_PATTERN.matcher(atomFeedContentValue);
    String atomFeedContent = EMPTY;
    if (matcher.find() && matcher.groupCount() == 1) {
      atomFeedContent = matcher.group(1);
    }
    return atomFeedContent;
  }

  private static JsonNode convertToTemplate(Map<String, String> map, String value) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(value);
    ObjectNode returnedNode = new ObjectNode(JsonNodeFactory.instance);
    Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.getFields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> mapEntry = iterator.next();
      String fieldName = mapEntry.getKey();
      String mappedName = map.get(fieldName);
      if (mappedName != null) {
        returnedNode.put(mappedName, mapEntry.getValue());
      } else {
        returnedNode.put(fieldName, mapEntry.getValue());
      }
    }
    return returnedNode;
  }

  private static Map<String, String> createTemplateMap(File templateFile) {
    Document document;
    try {
      SAXBuilder saxBuilder = new SAXBuilder();
      document = saxBuilder.build(templateFile);
    } catch (IOException | JDOMException e) {
      throw new DataException(e.getMessage());
    }

    Map<String, String> map = new HashMap<>();
    List<Element> children = document.getRootElement().getChildren();
    for (Element child : children) {
      String openLmisName = child.getAttributeValue("openlmis-name");
      String vendorName = child.getAttributeValue("vendor-name");
      map.put(openLmisName, vendorName);
    }
    return map;
  }

  public static String getEventFeed(EventFeedService eventFeedService,
                                    String requestURL,
                                    int feedNumber,
                                    String vendor,
                                    String category) {
    try {
      Feed feed = eventFeedService.getEventFeed(new URI(requestURL), category, feedNumber);
      try {
        mapFeedBasedOnVendorAndCategory(feed, vendor, category);
      } catch (IOException e) {
        logger.error(e);
      }

      return new WireFeedOutput().outputString(feed);

    } catch (URISyntaxException | FeedException e) {
      logger.error("error occurred while getting feed for feedNumber: " + feedNumber, e);
      throw new RuntimeException("Error serializing feed.", e);
    }
  }
}