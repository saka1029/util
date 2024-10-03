## HomeキーをBackSpaceに変更

```
sudo cp /usr/share/X11/xkb/symbols/pc /usr/share/X11/xkb/symbols/pc.bak
sudo vi /usr/share/X11/xkb/symbols/pc
```


```
  key <HOME> {[  Home    ]};
→
  key <HOME> {[  BackSpace    ]};
```

## Chrome拡張機能V2延長

```
sudo mkdir -p /etc/opt/chrome/policies/managed /etc/opt/chromium/policies/managed
echo '{ "ExtensionManifestV2Availability": 2 }' | sudo tee /etc/opt/chrome/policies/managed/policy.json /etc/opt/chromium/policies/managed/policy.json
```

## vimクリップボード

ヤンク(y)コマンドでクリップボードに貼り付けるにはvim-gtkが必要。
```
sudo apt install vim-gtk
or
sudo apt install vim-gtk3
```

.vimrcに以下を記述する。
```
set clipboard=unnamedplus
```

## vim ESCでIMEを抜ける

.vimrcに以下を追記。

```
augroup VIMRC
    if has('unix') " インサート・モードを抜けた時は必ず日本語 OFF
        autocmd InsertLeave,CmdwinLeave * call system('fcitx-remote -c')
    endif
augroup END
```

「autocmd」の行だけでもよい。
「has(x)」の引数は'win64`でwindowsとなる。

fcitx-remoteのオプションは以下のとおり。

```
Usage: fcitx-remote [OPTION]
	-c		inactivate input method
	-o		activate input method
	-r		reload fcitx config
	-t,-T		switch Active/Inactive
	-e		Ask fcitx to exit
	-a		print fcitx's dbus address
	-m <imname>	print corresponding addon name for im
	-s <imname>	switch to the input method uniquely identified by <imname>
	[no option]	display fcitx state, 0 for close, 1 for inactive, 2 for acitve
	-h		display this help and exit
```