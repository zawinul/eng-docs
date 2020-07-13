#!/bin/bash

cd /usr/src/app
tar -xzvf node_modules.tar.gz
node src/eng-registry.js
