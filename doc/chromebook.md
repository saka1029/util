# Chromebook

## SuzyQable

[usb - DIY Chromebook debug cable (SuzyQable / Suzy Q Cable) - Electrical Engineering Stack Exchange](https://electronics.stackexchange.com/questions/629357/diy-chromebook-debug-cable-suzyqable-suzy-q-cable)

![alt text](chromebook/image-3.png)

 Answer

I found the problem, this breakout board is available as 'Male' and 'Female' version, the pcb silkscreen is intended for the socket version (female) so the plug version (male) is basically reversed.

![alt text](chromebook/image.png)

The correct pinout for this specific breakout board is:

A5: D+
B5: D-
A8: 22K resistor to VCC
B8: 56K resistor to VCC
A fully working cable only needs the two resistors and the USB plug cable, additional joints for VCC/GND are not needed (at least on my device).

![alt text](chromebook/image-1.png)
![alt text](chromebook/image-2.png)

Share
Cite
Follow
edited Jul 30, 2022 at 21:34
answered Jul 30, 2022 at 21:29
chrome's user avatar
chrome
10111 silver badge66 bronze badges

## 配線

![alt text](chromebook/typec.jpg)

[上段]
|端子|接続|-| 
|-|-|-|
|SGND|-|-|
|GND|-|-|
|B2|-|-|
|B3|-|-|
|VCC|55KΩ|-|
|B5|白|-|
|D+|-|-|
|D-|-|-|
|B8|55KΩ|-|
|VCC|赤|-|
|B10|-|-|
|B11|-|-|
|GND|黒|-|

[下段]
|端子|接続|-|
|-|-|-|
|SGND|-|-|
|GND|-|-|
|A11|-|-|
|A10|-|-|
|VCC||-|
|A8|22KΩ|-|
|D-|-|-|
|D+|-|-|
|A5|緑|-|
|VCC|22KΩ|-|
|A3|-|-|
|A2|-|-|
|GND|-|-|