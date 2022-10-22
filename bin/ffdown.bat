@echo off
setlocal
::: 動画インデックスファイル(.m3u8)から動画をダウンロードします。
::: (1) 動画ゲッターで動画インデックスファイル(.m3u8)をダウンロードします。
::: (2) このバッチファイルを起動します。
:::     ffdown.bat 動画インデックスファイル.m3u8
:::     カレントディレクトリに動画ファイル(.mp4)がダウンロードされます。
if not "%~x1"==".m3u8" goto ERROR
ffmpeg -protocol_whitelist file,http,https,tcp,tls,crypto -i %1 -movflags faststart -c copy %~n1.mp4
goto END

:ERROR
echo usage: %0 INDEX_FILE.m3u8
goto END

:END
endlocal
