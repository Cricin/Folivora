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

import com.android.SdkConstants;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This converter keeps track app:drawableId values used
 * in project, and gives cached candidates when use drawableId
 */
public class DrawableIdConverter extends ResolvingConverter<String> {

  @NotNull
  @Override
  public Collection<? extends String> getVariants(ConvertContext convertContext) {
    if (convertContext == null) return Collections.emptyList();
    XmlFile file = convertContext.getFile();
    final Set<String> drawableIds = new HashSet<>();
    file.accept(new PsiRecursiveElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (element instanceof XmlTag) {
          String drawableId = ((XmlTag) element).getAttributeValue("drawableId", SdkConstants.AUTO_URI);
          if(drawableId != null && !drawableId.isEmpty()) {
            drawableIds.add(drawableId);
          }
        }
      }
    });
    return new ArrayList<>(drawableIds);
  }

  @Nullable
  @Override
  public String fromString(@Nullable String s, ConvertContext convertContext) {
    return s;
  }

  @Nullable
  @Override
  public String toString(@Nullable String s, ConvertContext convertContext) {
    return s;
  }

}
