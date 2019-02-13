/*
 * Copyright (C) 2019 Cricin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.android.dom;

import com.android.SdkConstants;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.XmlName;
import com.intellij.util.xml.reflect.DomExtension;

import org.jetbrains.android.dom.attrs.AttributeDefinition;
import org.jetbrains.android.dom.attrs.AttributeDefinitions;
import org.jetbrains.android.dom.attrs.AttributeFormat;
import org.jetbrains.android.dom.attrs.StyleableDefinition;
import org.jetbrains.android.dom.attrs.ToolsAttributeUtil;
import org.jetbrains.android.dom.converters.CompositeConverter;
import org.jetbrains.android.dom.converters.ManifestPlaceholderConverter;
import org.jetbrains.android.dom.converters.ResourceReferenceConverter;
import org.jetbrains.android.dom.layout.DataBindingElement;
import org.jetbrains.android.dom.layout.LayoutElement;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.facet.AndroidFacetConfiguration;
import org.jetbrains.android.resourceManagers.ModuleResourceManagers;
import org.jetbrains.android.resourceManagers.ResourceManager;

import java.lang.reflect.Method;
import java.util.Collection;

final class FolivoraAttrProcessing {
  private static final String[] FOLIVORA_STYLEABLE_NAMES = {
    "Folivora",
    "Folivora_Shape",
    "Folivora_Selector",
    "Folivora_Layer",
    "Folivora_Ripple",
    "Folivora_Level",
    "Folivora_Clip",
    "Folivora_Inset",
    "Folivora_Scale",
    "Folivora_Animation"
  };

  static void registerFolivoraAttributes(AndroidFacet facet,
                                         AndroidDomElement element,
                                         AttributeProcessingUtil.AttributeProcessor callback) {
    if (!(element instanceof LayoutElement)) return;
    if (element instanceof DataBindingElement) return;
    XmlTag tag = element.getXmlTag();
    if (isInvalidTagName(tag.getName())) return;
    if (hasFolivoraAttr(tag.getAttributes())) {
      for (String styleableName : FOLIVORA_STYLEABLE_NAMES) {
        registerAttributes(facet, element, styleableName, null, callback);
      }
    } else {
      registerAttributes(facet, element, "Folivora", null, callback);
    }
  }

  private static boolean hasFolivoraAttr(XmlAttribute[] attrs){
    for (XmlAttribute attr : attrs) {
      if ("drawableType".equals(attr.getLocalName()) || "setAs".equals(attr.getLocalName())) {
        return true;
      }
    }
    return false;
  }

  private static boolean isInvalidTagName(String tagName) {
    return "layout".equals(tagName)
      || "fragment".equals(tagName)
      || "include".equals(tagName)
      || "requestFocus".equals(tagName)
      || "merge".equals(tagName);
  }

  private static void registerAttributes(/*NotNull*/ AndroidFacet facet,
    /*NotNull*/ DomElement element,
    /*NotNull*/ String styleableName,
    /*Nullable*/ String resPackage,
    /*NotNull*/ AttributeProcessingUtil.AttributeProcessor callback) {
    ResourceManager manager = ModuleResourceManagers.getInstance(facet).getResourceManager
      (resPackage);
    if (manager == null) {
      return;
    }

    AttributeDefinitions attrDefs = manager.getAttributeDefinitions();
    if (attrDefs == null) {
      return;
    }

    String namespace = getNamespaceUriByResourcePackage(facet, resPackage);
    StyleableDefinition styleable = attrDefs.getStyleableByName(styleableName);
    if (styleable != null) {
      registerStyleableAttributes(element, styleable, namespace, callback);
    }
    // It's a good idea to add a warning when styleable not found, to make sure that code doesn't
    // try to use attributes that don't exist. However, current AndroidDomExtender code relies on
    // a lot of "heuristics" that fail quite a lot (like adding a bunch of suffixes to short
    // class names)
    // TODO: add a warning when rest of the code of AndroidDomExtender is cleaned up
  }

  /*Nullable*/
  private static String getNamespaceUriByResourcePackage(/*NotNull*/ AndroidFacet facet,
    /*Nullable*/ String resPackage) {
    if (resPackage == null) {
      if (!isAppProject(facet) || facet.requiresAndroidModel()) {
        return SdkConstants.AUTO_URI;
      }
      Manifest manifest = facet.getManifest();
      if (manifest != null) {
        String aPackage = manifest.getPackage().getValue();
        if (aPackage != null && !aPackage.isEmpty()) {
          return SdkConstants.URI_PREFIX + aPackage;
        }
      }
    } else if (resPackage.equals(SdkConstants.ANDROID_NS_NAME)) {
      return SdkConstants.ANDROID_URI;
    }
    return null;
  }

  private static Method sIsAppProjectMethod;

  private static boolean isAppProject(AndroidFacet facet) {
    if (sIsAppProjectMethod != null) {
      try {
        return (boolean) sIsAppProjectMethod.invoke(facet.getConfiguration(), (Object[]) null);
      } catch (Exception ignore) {
      }
    }
    try {
      return facet.isAppProject();
    } catch (NoSuchMethodError nsme) {
      AndroidFacetConfiguration conf = facet.getConfiguration();
      try {
        sIsAppProjectMethod = conf.getClass().getDeclaredMethod("isAppProject", (Class<?>[]) null);
        return (boolean) sIsAppProjectMethod.invoke(conf, (Object[]) null);
      } catch (Exception ignore) {
      }
    }
    return false;
  }

  private static void registerStyleableAttributes(
    /*NotNull*/ DomElement element,
    /*NotNull*/ StyleableDefinition styleable,
    /*Nullable*/ String namespaceUri,
    /*NotNull*/ AttributeProcessingUtil.AttributeProcessor callback) {
    for (AttributeDefinition attrDef : styleable.getAttributes()) {
      registerAttribute(attrDef, styleable.getName(), namespaceUri, element, callback);
    }
  }

  private static void registerAttribute(
    AttributeDefinition attrDef,
    String parentStyleableName,
    String namespaceKey,
    DomElement element,
    AttributeProcessingUtil.AttributeProcessor callback) {
    String name = attrDef.getName();
    if (!SdkConstants.ANDROID_URI.equals(namespaceKey) && name.startsWith(SdkConstants.ANDROID_NS_NAME_PREFIX)) {
      name = name.substring(SdkConstants.ANDROID_NS_NAME_PREFIX.length());
      namespaceKey = SdkConstants.ANDROID_URI;
    }

    XmlName xmlName = new XmlName(name, namespaceKey);
    DomExtension extension = callback.processAttribute(xmlName, attrDef, parentStyleableName);
    if (extension != null) {
      Converter converter = AndroidDomUtil.getSpecificConverter(xmlName, element);
      if (converter == null) {
        if (SdkConstants.TOOLS_URI.equals(namespaceKey)) {
          converter = ToolsAttributeUtil.getConverter(attrDef);
        } else {
          converter = AndroidDomUtil.getConverter(attrDef);
          if (converter != null && element.getParentOfType(Manifest.class, true) != null) {
            converter = new ManifestPlaceholderConverter(converter);
          }
        }
      }

      if (converter != null) {
        extension.setConverter(converter, mustBeSoft(converter, attrDef.getFormats()));
      }
    }
  }

  private static boolean mustBeSoft(Converter converter, Collection<AttributeFormat> formats) {
    if (!(converter instanceof CompositeConverter)
      && !(converter instanceof ResourceReferenceConverter)) {
      return formats.size() > 1;
    } else {
      return false;
    }
  }

  private FolivoraAttrProcessing() {}
}
