# Chromebook

## SuzyQable

[usb - DIY Chromebook debug cable (SuzyQable / Suzy Q Cable) - Electrical Engineering Stack Exchange](https://electronics.stackexchange.com/questions/629357/diy-chromebook-debug-cable-suzyqable-suzy-q-cable)

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