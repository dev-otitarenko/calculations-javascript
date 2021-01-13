// Math MIN
Math.MIN = function() {
    if (arguments.length == 0) return null;

    var ret = arguments[0], val, v;
    for(var i = 0; i < arguments.length; i++) {
        val = arguments[i]; v = parseFloat(val);
        if (!isNaN(v)) {
            if (ret > v) {
                ret = val;
            }
        }
    }
    return ret;
};
// Math MAX
Math.MAX = function() {
    if (arguments.length == 0) return null;

    var ret = arguments[0], val;
    for(var i = 0; i < arguments.length; i++) {
        val = arguments[i];
        if (!isNaN(parseFloat(val))) {
            val = 1*parseFloat(val);
            if (ret < val) {
                ret = val;
            }
        }
    }
    return ret;
};
// Math SUM
Math.SUM = function() {
    var ret = 0, val;
    for(var i = 0; i < arguments.length; i++) {
        val = arguments[i];
        if (!isNaN(parseFloat(val))) {
            ret += 1*val;
        }
    }
    return ret;
};
// Math AVG
Math.AVG = function() {
    /*
    var numbersArr = Array.prototype.slice.call(arguments);
        //--CALCULATE AVAREGE--
        var total = 0;
        for(var key in numbersArr)
            total += numbersArr[key];
        var meanVal = total / numbersArr.length;

        return meanVal;
     */
    var ret = 0, len = 0, val;
    for(var i = 0; i < arguments.length; i++) {
        val = arguments[i];
        if (!isNaN(parseFloat(val))) {
            val = parseFloat(val);
            if (val !== 0) {
                ret += val;
                len++;
            }
        }
    }
    return (len !== 0 ? ret/len : 0);
};