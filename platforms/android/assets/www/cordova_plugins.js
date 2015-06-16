cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-device/www/device.js",
        "id": "cordova-plugin-device.device",
        "clobbers": [
            "device"
        ]
    },
    {
        "file": "plugins/com.frankgreen/www/ACR-NFC-Reader-PhoneGap-Plugin.js",
        "id": "com.frankgreen.ACR-NFC-Reader-PhoneGap-Plugin",
        "clobbers": [
            "cordova.plugins.ACR-NFC-Reader-PhoneGap-Plugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-device": "1.0.1-dev",
    "com.frankgreen": "0.0.1"
}
// BOTTOM OF METADATA
});