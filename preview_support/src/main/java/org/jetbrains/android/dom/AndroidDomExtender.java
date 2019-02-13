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

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.reflect.DomExtender;
import com.intellij.util.xml.reflect.DomExtensionsRegistrar;

import org.jetbrains.android.dom.attrs.AttributeFormat;
import org.jetbrains.android.dom.resources.ResourceValue;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.Set;


/**
 * Hook implementation of {@link DomExtender}, injecting Folivora attrs to the
 * xml tag element, thus the IDE can provide code completion about these attrs
 */
public class AndroidDomExtender extends DomExtender<AndroidDomElement> {

  private static Class legacyAttributeFormatClass;
  private static Class attributeFormatClass;

  static {
    try {
      legacyAttributeFormatClass = Class.forName("org.jetbrains.android.dom.attrs.AttributeFormat");
    } catch (ClassNotFoundException ignore) {
    }
    try {
      attributeFormatClass = Class.forName("com.android.ide.common.rendering.api.AttributeFormat");
    } catch (ClassNotFoundException ignore) {
    }
  }


  @Override
  public boolean supportsStubs() {
    return false;
  }

  private static Class getValueClass(Object format) {
    if (format == null) return String.class;
    if (legacyAttributeFormatClass != null && legacyAttributeFormatClass.isInstance(format)) {
      if (AttributeFormat.Boolean == format) {
        return boolean.class;
      } else if (AttributeFormat.Reference == format
        || AttributeFormat.Dimension == format
        || AttributeFormat.Color == format) {
        return ResourceValue.class;
      } else {
        return String.class;
      }
    } else if (attributeFormatClass != null && attributeFormatClass.isInstance(format)) {
      if (com.android.ide.common.rendering.api.AttributeFormat.BOOLEAN == format) {
        return boolean.class;
      } else if (com.android.ide.common.rendering.api.AttributeFormat.REFERENCE == format
        || com.android.ide.common.rendering.api.AttributeFormat.DIMENSION == format
        || com.android.ide.common.rendering.api.AttributeFormat.COLOR == format) {
        return ResourceValue.class;
      } else {
        return String.class;
      }
    }
    return String.class;
  }

  @Override
  public void registerExtensions(AndroidDomElement element,
                                 final DomExtensionsRegistrar registrar) {
    final AndroidFacet facet = AndroidFacet.getInstance(element);

    if (facet == null) {
      return;
    }

    AttributeProcessingUtil.AttributeProcessor callback = (xmlName, attrDef, parentStyleableName) -> {
      Set<?> formats = attrDef.getFormats();
      Class valueClass = formats.size() == 1 ? getValueClass(formats.iterator().next()) : String.class;
      registrar.registerAttributeChildExtension(xmlName, GenericAttributeValue.class);
      return registrar.registerGenericAttributeValueChildExtension(xmlName, valueClass);
    };

    AttributeProcessingUtil.processAttributes(element, facet, true, callback);
    try {
      FolivoraAttrProcessing.registerFolivoraAttributes(facet, element, callback);
    } catch (Exception ignore) {}

    SubtagsProcessingUtil.processSubtags(facet, element,
      registrar::registerCollectionChildrenExtension);
  }

}
