var args = WScript.Arguments;
var all = "";
for (i = 0; i < args.length; i++)
    all += " " + args(i);
WScript.Echo(eval(all));

// デフォルトのエンジンをCScriptにしてロゴ表示しないようにする。
// cscript //nologo //h:cscript //s
