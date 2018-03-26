# datePicker
## 描述
一个适合移动端的日期选择插件，支持ios,andriod,PC端主流浏览器
## DEMO
[demo](http://ddd702.github.io/datePicker)
## 依赖(由于iscroll5下滑动反弹太严重，换成iscroll4)
jquery 1.9+或者Zepto,iscroll4
## 使用：
引入插件脚本和样式后,使用类似：$('#date').datePicker(opt);
## opt参数
#### beginyear
开始年份(默认2010开始)

#### endyear
结束年份（默认到2020）

#### days
数组，默认['周日','周一','周二','周三','周四','周五','周六']

#### beginhour
开始的小时

#### endhour
结束的小时

#### beginminute
开始的分钟

#### endminute
结束的分钟

#### curdate
布尔值,若为true,插件初始化时始终指向当前时间，若为false，初始化时指向input的value值,默认为false

#### theme
- date:可选择年，月，日
- datetime:可选择年，月，日，小时，分钟
- time:可选择小时，分钟
- month:可选择年，月

#### event
调出插件的事件,默认为click

#### scrollOpt
iscroll4滚动设置,详情参见[iscroll4](http://cubiq.org/iscroll-4)

#### callBack
回调函数，按确认后执行的函数，默认function(){}

