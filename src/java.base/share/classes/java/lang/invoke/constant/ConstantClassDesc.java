/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.lang.invoke.constant;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Optional;

import static java.lang.invoke.constant.ConstantDescs.BSM_CLASSDESC;
import static java.lang.invoke.constant.ConstantDescs.CR_ClassDesc;
import static java.lang.invoke.constant.ConstantUtils.dropFirstAndLastChar;
import static java.lang.invoke.constant.ConstantUtils.internalToBinary;
import static java.util.Objects.requireNonNull;

/**
 * A <a href="package-summary.html#nominal">nominal descriptor</a> for a class,
 * interface, or array type.  A {@linkplain ConstantClassDesc} corresponds to a
 * {@code Constant_Class_info} entry in the constant pool of a classfile.
 */
public final class ConstantClassDesc implements ClassDesc {
    private final String descriptor;

    /**
     * Create a {@linkplain ClassDesc} from a descriptor string for a class or
     * interface type
     *
     * @param descriptor a field descriptor string for a class or interface type,
     *                   as per JVMS 4.3.2
     * @throws IllegalArgumentException if the descriptor string is not a valid
     * field descriptor string, or does not describe a class or interface type
     * @jvms 4.3.2 Field Descriptors
     */
    ConstantClassDesc(String descriptor) {
        requireNonNull(descriptor);
        int len = ConstantUtils.matchSig(descriptor, 0, descriptor.length());
        if (len == 0 || len == 1
            || len != descriptor.length())
            throw new IllegalArgumentException(String.format("not a valid reference type descriptor: %s", descriptor));
        this.descriptor = descriptor;
    }

    @Override
    public String descriptorString() {
        return descriptor;
    }

    @Override
    public Class<?> resolveConstantDesc(MethodHandles.Lookup lookup)
            throws ReflectiveOperationException {
        ClassDesc c = this;
        int depth = ConstantUtils.arrayDepth(descriptorString());
        for (int i=0; i<depth; i++)
            c = c.componentType();

        if (c.descriptorString().length() == 1)
            return lookup.findClass(descriptorString());
        else {
            Class<?> clazz = lookup.findClass(internalToBinary(dropFirstAndLastChar(c.descriptorString())));
            for (int i = 0; i < depth; i++)
                clazz = Array.newInstance(clazz, 0).getClass();
            return clazz;
        }
    }

    @Override
    public Optional<? extends ConstantDesc<ConstantDesc<Class<?>>>> describeConstable() {
        return Optional.of(DynamicConstantDesc.<ConstantDesc<Class<?>>>of(BSM_CLASSDESC, CR_ClassDesc)
                                   .withArgs(descriptor));
    }

    /**
     * Constant bootstrap method for representing a {@linkplain ClassDesc} in
     * the constant pool of a classfile.
     *
     * @param lookup ignored
     * @param name ignored
     * @param clazz ignored
     * @param descriptor a field descriptor string for the class, as per JVMS 4.3.2
     * @return the {@linkplain ClassDesc}
     * @jvms 4.3.2 Field Descriptors
     */
    public static ClassDesc constantBootstrap(MethodHandles.Lookup lookup, String name, Class<ClassDesc> clazz,
                                              String descriptor) {
        return ClassDesc.ofDescriptor(descriptor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDesc constant = (ClassDesc) o;
        return Objects.equals(descriptor, constant.descriptorString());
    }

    @Override
    public int hashCode() {
        return descriptor != null ? descriptor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("ClassDesc[%s]", displayName());
    }
}
