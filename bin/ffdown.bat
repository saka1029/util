@echo off
setlocal
::: ����C���f�b�N�X�t�@�C��(.m3u8)���瓮����_�E�����[�h���܂��B
::: (1) ����Q�b�^�[�œ���C���f�b�N�X�t�@�C��(.m3u8)���_�E�����[�h���܂��B
::: (2) ���̃o�b�`�t�@�C�����N�����܂��B
:::     ffdown.bat ����C���f�b�N�X�t�@�C��.m3u8
:::     �J�����g�f�B���N�g���ɓ���t�@�C��(.mp4)���_�E�����[�h����܂��B
if not "%~x1"==".m3u8" goto ERROR
ffmpeg -protocol_whitelist file,http,https,tcp,tls,crypto -i %1 -movflags faststart -c copy %~n1.mp4
goto END

:ERROR
echo usage: %0 INDEX_FILE.m3u8
goto END

:END
endlocal
