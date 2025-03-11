#!/usr/bin/env bash
cd bootloader
nasm ./fs/boot/loader.asm -Ov
unix2dos ./fs/splash.txt
mkisofs -boot-load-seg 0x0000050 -R -J -c boot/bootcat -b boot/loader -no-emul-boot -boot-load-size 4 -o bootable.iso ./fs/
qemu-system-i386 -cdrom ./bootable.iso -s -m 5M