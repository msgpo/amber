#
# Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
#
# This code is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 only, as
# published by the Free Software Foundation.  Oracle designates this
# particular file as subject to the "Classpath" exception as provided
# by Oracle in the LICENSE file that accompanied this code.
#
# This code is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# version 2 for more details (a copy is included in the LICENSE file that
# accompanied this code).
#
# You should have received a copy of the GNU General Public License version
# 2 along with this work; if not, write to the Free Software Foundation,
# Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
# or visit www.oracle.com if you need additional information or have any
# questions.
#


###############################################################################
#
# Unit tests for the configure script
#
###############################################################################

AC_DEFUN([TEST_STRING_OPS],
[
  FEW="ninja banan pepparkaka"
  MANY="banan antarktis pepparkaka ninjamask bana"

  EXPECTED_NON_MATCHING="antarktis ninjamask bana"
  UTIL_GET_NON_MATCHING_VALUES(ACTUAL, $MANY, $FEW)
  UTIL_ASSERT_STRING_EQUALS($ACTUAL, $EXPECTED_NON_MATCHING, \
      [UTIL_GET_NON_MATCHING_VALUES failed])

  EXPECTED_MATCHING="banan pepparkaka"
  UTIL_GET_MATCHING_VALUES(ACTUAL, $FEW, $MANY)
  UTIL_ASSERT_STRING_EQUALS($ACTUAL, $EXPECTED_MATCHING, \
      [UTIL_GET_MATCHING_VALUES failed])
])

# Use the CUSTOM_EARLY_HOOK to inject our test after basic init is done.
AC_DEFUN_ONCE([CUSTOM_EARLY_HOOK],
[
  $PRINTF "\nStarting configure tests\n"
  $PRINTF "==============================\n"

  TEST_STRING_OPS

  # If no assertions failed, report success
  $PRINTF "==============================\n"
  $PRINTF "Configure tests finished successfully\n\n"
  exit 0
])
