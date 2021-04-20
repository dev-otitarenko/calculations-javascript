var Doc = {
    // ...
  value: function (nm, tabNum, rowNum, v) {
    var f;
    if (v === undefined) { // get
         for (var i in data) {
             f = data[i];
             if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {
                 return f.val;
             }
         }
         return null;
     } else { // set
         for (var i in data) {
             f = data[i];
             if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {
                 f.val = 1*v;
                 f.dirty = true;
                 data[i] = f;
                 return;
             }
         }
         data.push({ field: nm, tabn: tabNum, nrow: rowNum, val: v });
      }
  },
    // ...
  changed: function(nm, tabNum, rowNum) {
       for (var i in data) {
           f = data[i];
           if (f.field == nm && f.tabn == tabNum && f.nrow == rowNum) {
                console.log('Dirty: ', nm, tabNum, rowNum, f.dirty);
               return f.dirty;
           }
       }
       return false;
  }
}