# Util

## iv
Simple image viewer by Java

### 操作

* プログラム終了: ESC, Q, X
* 全画面切り替え: F, Enter
* 前へ: ←, (ウィンドウの左端をクリック), 「,」(カンマ)
* 次へ: →, (ウィンドウの右端をクリック), 「.」(ピリオド)
* 先頭へ: 0
* 末尾へ: 9
* 右90度回転: R
* 左90度回転: L

画面中央をクリックするとコンテキストメニューが表示されるので、そこからも操作できます。

### EXE化

以下を使用してJarファイルをWindowsのEXEに変換します。

[exewrap](https://exewrap.osdn.jp/)

プロジェクトフォルダで以下を実行するとbinディレクトリにIV.exeが作成されます。

    mvn package
    makeIv
