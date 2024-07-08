# android

## droidvim

## termux

「github termux-app」で検索する。
ページ右側に「Releases」のリンクがあるのでクリック。

アーキテクチャで.apkファイルを選択する。

[termux-app_v0.118.1+github-debug_arm64-v8a.apk](https://github.com/termux/termux-app/releases/download/v0.118.1/termux-app_v0.118.1+github-debug_arm64-v8a.apk)


### vscode

vscodeのサーバをインストールするのであれば
ubuntuは不要らしい。

ubuntuなしでインストールする場合

[Installing Visual Studio Code (code-server) on Android Devices (Termux) · GitHub](https://gist.github.com/lexavey/e6efe6853e7f978d939c24e8fd2650fc)

```
pkg update -y
pkg install tur-repo
pkg install code-server
code-server --version
code-server --auth none
```

vimのプラグインはインストールできる。
Java関係のプラグイン(例えば`Project Manager for Java`)は

"The `Project Manager for Java` extension is not available in code-server for Web.

などとなる。

Terminalを開いて`mvn package`などは実行できる。

### git

```
pkg git
```

git version 2.45.2がインストールされた。

### Java17

```
pkg openjdk-17
```

openjdk version "17-internal" 2021-09-14
がインストールされた。


### maven

```
pkg maven
```

Apache Maven 3.9.8がインストールされた。


### ディレクトリ構造

termuxユーザのホームディレクトリ

```
~
  .bashrc  --> 中身は「./start-ubuntu.sh」
  .termux/
    termux.properties
  start-ubuntu.sh
  ubuntu.sh
  storage/ --> androidファイルシステムのルート
    dcim/
    downloads/ --> androidのダウンロードフォルダ
    movies/
    music/
    pictures/
    shared/
  ubuntu-binds/
  ubuntu-fs/ --> ubuntuファイルシステムのルート
    bin/
    boot/
    dev/
    etc/
    home/
    lib/
    media/
    mnt/
    opt/
    proc/
    root/
    run/
    sbin/
    srv/
    sys/
    tmp/
    usr/
    var/
```

android <-> Linux 間でファイルのコピーができる。