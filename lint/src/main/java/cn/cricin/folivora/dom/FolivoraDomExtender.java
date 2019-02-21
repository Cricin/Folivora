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

package cn.cricin.folivora.dom;

import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.reflect.DomExtender;
import com.intellij.util.xml.reflect.DomExtenderEP;
import com.intellij.util.xml.reflect.DomExtensionsRegistrar;
import org.jetbrains.android.dom.AndroidDomElement;
import org.jetbrains.android.dom.AttributeProcessingUtil;
import org.jetbrains.android.dom.attrs.AttributeFormat;
import org.jetbrains.android.dom.converters.PackageClassConverter;
import org.jetbrains.android.dom.resources.ResourceValue;
import org.jetbrains.android.facet.AndroidFacet;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Set;

public final class FolivoraDomExtender extends DomExtender<AndroidDomElement> {

  private static final PackageClassConverter DRAWABLE_CLASS_CONVERTER =
    new PackageClassConverter("android.graphics.drawable.Drawable");
  private static Class sLegacyAttributeFormatClass;
  private static Class sAttributeFormatClass;

  static {
    try {
      sLegacyAttributeFormatClass = Class.forName("org.jetbrains.android.dom.attrs.AttributeFormat");
    } catch (ClassNotFoundException ignore) {
    }
    try {
      sAttributeFormatClass = Class.forName("com.android.ide.common.rendering.api.AttributeFormat");
    } catch (ClassNotFoundException ignore) {
    }
  }

  @Override
  public boolean supportsStubs() {
    return false;
  }

  private static Class getValueClass(Object format) {
    if (format == null) return String.class;
    if (sLegacyAttributeFormatClass != null && sLegacyAttributeFormatClass.isInstance(format)) {
      if (AttributeFormat.Boolean == format) {
        return boolean.class;
      } else if (AttributeFormat.Reference == format
        || AttributeFormat.Dimension == format
        || AttributeFormat.Color == format) {
        return ResourceValue.class;
      } else {
        return String.class;
      }
    } else if (sAttributeFormatClass != null && sAttributeFormatClass.isInstance(format)) {
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
  public void registerExtensions(@Nonnull AndroidDomElement element,
                                 @Nonnull final DomExtensionsRegistrar registrar) {
    final AndroidFacet facet = AndroidFacet.getInstance(element);

    if (facet == null) {
      return;
    }

    AttributeProcessingUtil.AttributeProcessor callback = (xmlName, attrDef, parentStyleableName)
      -> {
      Set<?> formats = attrDef.getFormats();
      Class valueClass = formats.size() == 1 ? getValueClass(formats.iterator().next()) : String
        .class;
      registrar.registerAttributeChildExtension(xmlName, GenericAttributeValue.class);
      return registrar.registerGenericAttributeValueChildExtension(xmlName, valueClass);
    };

    try {
      FolivoraAttrProcessing.registerFolivoraAttributes(facet, element, callback);
    } catch (Exception ignore) {}
  }


  public static void install() {
    ExtensionPoint<DomExtenderEP> point = Extensions.getRootArea().getExtensionPoint
      (DomExtenderEP.EP_NAME);
    DomExtenderEP androidDomExtenderEp = null;
    DomExtenderEP[] eps = point.getExtensions();
    for (DomExtenderEP ep : eps) {
      if (ep.extenderClassName.endsWith("AndroidDomExtender")) {
        androidDomExtenderEp = ep;
        break;
      }
    }
    if (androidDomExtenderEp != null) {
      DomExtenderEP ep = new DomExtenderEP();
      ep.setPluginDescriptor(androidDomExtenderEp.getPluginDescriptor());
      ep.domClassName = AndroidDomElement.class.getCanonicalName();
      ep.extenderClassName = FolivoraDomExtender.class.getCanonicalName();
      try {
        Field field = ep.getClass().getDeclaredField("myExtender");
        field.setAccessible(true);
        field.set(ep, new FolivoraDomExtender());
        point.registerExtension(ep);
      } catch (Exception ignore) {}
    }
  }
}
