//设置文本显示长度    http://www.cnblogs.com/leepyng/p/6003447.html
MetronicApp.filter('textLengthSet', function() {
    return function(value, wordwise, max, tail) {
        if (!value) return '';

        max = parseInt(max, 10);
        if (!max) return value;
        if (value.length <= max) return value;

        value = value.substr(0, max);
        /*if (wordwise) {
         处理以空格为末尾的文本
         var lastspace = value.lastIndexOf(' ');
         if (lastspace != -1) {
         value = value.substr(0, lastspace);
         }
         }*/

        return value + (tail );//'...'可以换成其它文字
    };
})





//采集配置模板显示过滤
MetronicApp.filter("templateType",function(){
    return function(input){
        var out = "";
        if (input=="user-singlepage"){
            out="单页采集";
        }
        if (input=="user-listpage"){
            out="分页采集";
        }
        if (input=="user-multipage"){
            out="深度采集";
        }
        if (input.indexOf("system")>=0){
            out="系统配置";
        }

        return out;
    }
});









// http://sparkalow.github.io/angular-truncate/
//文本截取
MetronicApp
    .filter('characters', function () {
        return function (input, chars, breakOnWord) {
            if (isNaN(chars)) return input;
            if (chars <= 0) return '';
            if (input && input.length > chars) {
                input = input.substring(0, chars);

                if (!breakOnWord) {
                    var lastspace = input.lastIndexOf(' ');
                    //get last space
                    if (lastspace !== -1) {
                        input = input.substr(0, lastspace);
                    }
                }else{
                    while(input.charAt(input.length-1) === ' '){
                        input = input.substr(0, input.length -1);
                    }
                }
                return input + '...';
            }
            return input;
        };
    })
    .filter('splitcharacters', function() {
        return function (input, chars) {
            if (isNaN(chars)) return input;
            if (chars <= 0) return '';
            if (input && input.length > chars) {
                var prefix = input.substring(0, chars/2);
                var postfix = input.substring(input.length-chars/2, input.length);
                return prefix + '...' + postfix;
            }
            return input;
        };
    })
    .filter('words', function () {
        return function (input, words) {
            if (isNaN(words)) return input;
            if (words <= 0) return '';
            if (input) {
                var inputWords = input.split(/\s+/);
                if (inputWords.length > words) {
                    input = inputWords.slice(0, words).join(' ') + '…';
                }
            }
            return input;
        };
    });










