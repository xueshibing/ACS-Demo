var exec = require('cordova/exec');

function ACR(){ 
}

ACR.TagSuccessListener = function(){};
ACR.TagFailureListener = function(){};

ACR.start = function() {
    if (cordova.platformId === "android") {
        setTimeout(
            function () {
                cordova.exec( 
                  function(r){
                    ACR.metadata = ACR.convertMetadata(r);
                    r.metadata = ACR.metadata;
                    ACR.TagSuccessListener(r);
                  },
                  function(r){
                    ACR.metadata = {};
                    ACR.TagFailureListener(r);
                  },
                    "ACRNFCReaderPhoneGapPlugin", "listen", []); 
            }, 10
        );
    }
}

//document.addEventListener('deviceready', ACR.handleFromIntentFilter, false);

ACR.convertMetadata = function(r){
  var h = {};
  if(r.data =="3B80800101"){
    h.type = "JavaCard";
  }else if(r.historical){
    var t = r.historical.slice(10,14)
      if (t == "0001"){
        h.type = "Mifare 1K"
      }else if(t == "0002"){
        h.type = "Mifare 4K"
      }else if(t == "0003"){
        h.type = "Mifare Ultralight"
      }else if(t == "0026"){
        h.type = "Mifare Mini"
      }else if(t == "F004"){
        h.type = "Topaz and Jewel"
      }else if(t == "F011"){
        h.type = "FeliCa 212K"
      }else if(t == "F012"){
        h.type = "FeliCa 424K"
      }else if(t == "F028"){
        h.type = "JCOP 30"
      }
  }
  return h;
}

ACR.AID = "F222222228";
ACR.setAID = function (aid) {
  ACR.AID = aid;
}

ACR.metadata = {};
ACR.runCardAbsent = function () {
  ACR.metadata = {};
  ACR.onCardAbsent();
}

ACR.clearLCD = function (success, failure) {
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "clearLCD", []);
}

ACR.display = function (msg, opts, success, failure) {
  var options = opts || {}
  if(options.bold == undefined){options.bold = false}
  if(options.font == undefined){options.font = 1}
  if(options.x == undefined){options.x = 0}
  if(options.y == undefined){options.y = 0}
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "display", [msg, options.x, options.y, options.bold, options.font]);
}

ACR.addTagListener = function (success, failure) {
  ACR.TagSuccessListener = success;
  ACR.TagFailureListener = failure;
  //cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "listen", []);
}

ACR.authenticateWithKeyA = function(block,keyA,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "authenticateWithKeyA", [block,keyA]);
}

ACR.selectFile = function(aid,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "selectFile", [aid]);
}

ACR.authenticateWithKeyB = function(block,keyB,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "authenticateWithKeyB", [block,keyB]);
}

ACR.writeAuthenticate = function(block,keyA, keyB,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "writeAuthenticate", [block,keyA,keyB]);
}

ACR.readUID = function(success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "readUID",[]);
}
ACR.readData = function(block,success,failure){
  if(ACR.metadata.type == "JavaCard"){
    ACR.selectFile(ACR.AID,success,failure);
  }else{
    cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "readData", [block]);
  }
}
ACR.writeData = function(block, data,success,failure){
  if(ACR.metadata.type == "JavaCard"){
    failure({success:false, exception: "JavaCard"});
  }else{
    cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "writeData", [block,data]);
  }
}
ACR.onCardAbsent = function () {
}
ACR.onReady = function (reader) {
}
ACR.onAttach = function (device) {
}
ACR.onDetach = function (device) {
}
window.ACR = ACR;
