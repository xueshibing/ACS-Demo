cordova.define("com.shinetechchina.coffee.nfc.CoffeeNFC", function(require, exports, module) { var exec = require('cordova/exec');

function CoffeeNFC(){

}

CoffeeNFC.prototype.coolMethod = function(arg0, success, error) {
    exec(success, error, "CoffeeNFC", "coolMethod", [arg0]);
};

window.CoffeeNFC = new CoffeeNFC();

});
