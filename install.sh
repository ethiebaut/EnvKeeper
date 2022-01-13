#!/usr/bin/env bash

#
# Copyright (c) 2022 Eric Thiebaut-George.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

set -e

echo Generating random JWT secret
JWT_PASS=`openssl rand -base64 512 | tr -d '\n'`

echo Generating random PostgreSQL secret
PG_PASS=`dd if=/dev/urandom count=1 2> /dev/null | uuencode -m - | sed -ne 2p | cut -c-16`

echo Getting Event Hubs details
# If this fails, you probably didn't follow the README.md file and forgot to set these:
# export AZ_SUBSCRIPTION=12345678-1234-1234-1234-123456789012
# export AZ_RESOURCE_GROUP=my-azure-rg
# export EH_HUB_NS_NAME=build-notification
# export EH_HUB_NS_ACCESS_POLICY=listener
# export EH_HUB_NAME=build-notifications
EH_URL=${EH_HUB_NS_NAME}.servicebus.windows.net:9093
EH_CNX_STRING=`az eventhubs eventhub authorization-rule keys list --subscription ${AZ_SUBSCRIPTION} --resource-group ${AZ_RESOURCE_GROUP} --namespace-name ${EH_HUB_NS_NAME} --eventhub-name ${EH_HUB_NAME} --name ${EH_HUB_NS_ACCESS_POLICY} --query primaryConnectionString --output tsv`

echo Installing Helm chart
cd charts
helm install envkeeper -f ../values.yaml --set "jwtSecret=$JWT_PASS" --set "pgSecret=$PG_PASS" \
    --set "eventHubsLocation=EH_URL" --set "eventHubsName=EH_HUB_NAME" --set "eventHubsConnectionString=EH_CNX_STRING"

retVal=$?

if [ $retVal -ne 0 ] ; then
    echo -e "Error installing helm chart.\n"
    exit $retVal
fi

echo All done
