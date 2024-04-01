syntax enable
colorscheme blue
set showmode
set showmatch
set shiftwidth=4
set tabstop=4
set expandtab
" set encoding=cp932
" set directory=c:\temp
" set undodir=c:\temp
set nobackup
set noswapfile
set fileformats=unix,dos
"set fileformat=dos
set ignorecase
set belloff=all
"set guifont=MS_Gothic:h12
"set guifont=MS_Gothic:h12 
set guifont=Noto\ Mono\ 12

" CTRL-X are Cut
"vnoremap <C-X> "+x
" CTRL-C are Copy
"vnoremap <C-C> "+y
" CTRL-V are Paste
"map <C-V>       "+gP
"set imdisable
"function! ImInActivate()
"  call system('fcitx-remote -c')
"endfunction
inoremap <silent> <C-[> <ESC>:call system('fcitx-remote -c')<CR>
