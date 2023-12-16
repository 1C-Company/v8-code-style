#!/bin/bash
#*******************************************************************************
# Copyright (C) 2020 1C-Soft LLC and others.
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     1C-Soft LLC - initial API and implementation
#*******************************************************************************

current_version='0.5.0'
new_version='0.6.0'
next_version='0.7.0'

find . -name 'pom.xml' -exec sed -i '' "s/${current_version}-SNAPSHOT/${new_version}-SNAPSHOT/g" {} +

find . -name 'MANIFEST.MF' -exec sed -i '' "s/Bundle-Version: ${current_version}.qualifier/Bundle-Version: ${new_version}.qualifier/g" {} +

find . -name 'MANIFEST.MF' -exec sed -i '' "s/\(com\.e1c\.v8codestyle[\.a-z0-9]*;version=\"\)\([\.0-9]*\)\"/\1${new_version}\"/g" {} +

find . -name 'MANIFEST.MF' -exec sed -i '' "s/\(com\.e1c\.v8codestyle[\.a-z0-9]*;version=\"\[\)\([\.,0-9]*)\"\)/\1${new_version},${next_version})\"/g" {} +
find . -name 'MANIFEST.MF' -exec sed -i '' "s/\(com\.e1c\.v8codestyle[\.a-z0-9]*;bundle-version=\"\[\)\([\.,0-9]*)\"\)/\1${new_version},${next_version})\"/g" {} +

find . -name 'category.xml' -exec sed -i '' "s/${current_version}.qualifier/${new_version}.qualifier/g" {} +

find . -name 'feature.xml' -exec sed -i '' "s/version=\"${current_version}.qualifier\"/version=\"${new_version}.qualifier\"/g" {} +
