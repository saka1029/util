# git

## ログイン

このコマンドを打った後、認証をすると、次回から認証が省略される。

```
git config --global credential.helper store
```

→ ~/.git-credentialsにパスワードが記録される。

あるいは~/.netrcに以下を記述

```
machine github.com
login saka1029
password パスワード(アクセストークン)
```

Windowsの場合は以下が推奨されているらしい。

```
git config --global credential.credentialStore wincredman
```


## 文字化け

マルチバイト文字の8進エンコードを避けるには
以下のコマンドを入力する。

```
git config --global core.quotepath false
```

## GitHub Desktopで改行コードを自動変更しない設定

C:\Users\saka1\AppData\Local\GitHubDesktop\app-2.9.12\resources\app\git\etc\gitconfig
を以下のように変更する。

```
[core]
	symlinks = false
	autocrlf = false       <-- trueからfalseへ
	fscache = true
```

