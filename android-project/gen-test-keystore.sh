#!/bin/bash

# 生成测试用 Keystore
# 仅用于 GitHub Actions 自动构建测试，不应用于生产环境

KEYSTORE_FILE="app/release-keystore.jks"
KEYSTORE_PASSWORD="test123456"
KEY_ALIAS="test-key"
KEY_PASSWORD="test123456"

# 删除旧的 keystore（如果存在）
if [ -f "$KEYSTORE_FILE" ]; then
    rm "$KEYSTORE_FILE"
fi

# 生成新的 keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=Test, OU=Test, O=Test, L=Test, S=Test, C=CN"

echo "Keystore generated: $KEYSTORE_FILE"
echo "Store Password: $KEYSTORE_PASSWORD"
echo "Key Alias: $KEY_ALIAS"
echo "Key Password: $KEY_PASSWORD"
