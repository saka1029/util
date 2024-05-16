# Open Media Vault

## インストール

### BIOS設定

`Secure boot`を`Disable`にする。

### ソフトウェアソース

`ftp.jaist.ac.jp`はうまく行かない。
`debian.jp`を選択する。


### 起動後の設定

`root`でログインし、`omv_firstaid`コマンドで
`1 Configure Network Interface`を選択して、
ネットワークの再設定を行う。

## qBittorrent

```
sudo apt install qbittorrent-nox
sudo adduser qbtuser
(ENTER)  # for no password
```

```
sudo su qbtuser
qbittorrent-nox
```
ブラウザで`http://HOST:8080`にアクセスして
qBttorrentの設定を変更する。

手動でのサービス開始

```
sudo systemctl start qbittorrent-nox@qbtuser
```

起動時にサービスが開始されるようにする。

```
sudo systemctl enable qbittorrent-nox@qbtuser
```