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
        "file": "plugins/com.shinetechchina.coffee.nfc/www/CoffeeNFC.js",
        "id": "com.shinetechchina.coffee.nfc.CoffeeNFC",
        "clobbers": [
            "cordova.plugins.CoffeeNFC"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-device": "1.0.1-dev",
    "com.shinetechchina.coffee.nfc": "0.0.1"
}
// BOTTOM OF METADATA
});