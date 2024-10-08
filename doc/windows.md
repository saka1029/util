# Windows

## BitLocker

|回復キー |デバイス名 |キー ID |回復キー |[ドライブ] |キーのアップロード日|
|-|-|-|-|-|-|
|DESKTOP-UH7NL6E |B405A53C |455037-460779-399311-629255-478225-143572-417516-246048 |OSV |2023/11/29 12:35:14|


## Chrome拡張機能V2延長

```
Windows Registry Editor Version 5.00

[HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Google\Chrome]
"ExtensionManifestV2Availability"=dword:00000002
```

## Windows10でログイン時ロック画面を表示しない

```
Windows Registry Editor Version 5.00

[HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Microsoft\Windows\Personalization]
"NoLockScreen"=dword:00000001
```
