org 0x500
bits 16

loader16:
    xor ax, ax
    mov ds, ax
    mov ss, ax
    mov sp, 0x7BFF
    call int13_extensions_check_procedure
    mov si, pvd_disk_address_packet
    call int13_extended_read_procedure
    mov eax, DWORD [0x7E00+156+2]
    mov DWORD [si+8], eax
    call int13_extended_read_procedure
    pusha
    mov eax, "SPLA"
    call iso_9660_directory_read
    mov esi, DWORD [si+4]
    call int10_echo_tm_str_procedure
    popa
    mov eax, "KERN"
    call iso_9660_directory_read
    mov si, [si + 4]
    mov al, BYTE [esi + 0x04]
    cmp al, 1
    jz .kernel_x86_check
    mov di, int10_msg_kernel_64
    jmp .kernel_error
.kernel_x86_check:
    mov ax, WORD [esi + 0x12]
    cmp ax, 3
    jz .kernel_ok
    mov di, int10_msg_kernel_86
.kernel_error:
    mov si, int10_msg_kernel
    call int10_echo_tm_str_procedure
    xchg di, si
    jmp print_error
.kernel_ok:
    cli
    mov eax, cr0
    or eax, 0x1
    mov cr0, eax
    lgdt [gdtloc]
    jmp 0x08:loader32
bits 32
loader32:
    mov eax, 0x10
    mov ds, eax
    mov es, eax
    mov fs, eax
    mov gs, eax
    mov ss, eax
; Program Header Reading
    push DWORD [esi + 0x18] ; entry point
.program_headers:
    movzx ecx, WORD [esi + 0x2C] ; entry count
    mov edx, DWORD [esi + 0x1C] ; first entry offset in file
    add edx, esi
.program_header_action:
    mov edi, DWORD [edx + 0x00] ; type
    sub edi, 1
    jnz .next_program_header
    add edi, DWORD [edx + 0x0C] ; physical address
    jz .next_program_header
    pusha
    mov ecx, DWORD [edx + 0x10] ; file image size
    add esi, DWORD [edx + 0x04] ; file image offset
    rep movsb
    popa
.next_program_header:
    movzx eax, WORD [esi + 0x2A]
    add edx, eax
    loop .program_header_action
.kernel_jump:
    pop esi
    jmp esi
bits 16
;;;;;;;;;;;;;;;;;;;;;;;;; GDT
gdtloc: dw 0x0024
    dd gdt_nl
gdt_nl: dq 0x0000000000000000
gdt_kc: dq 0x00CF9A000000FFFF
gdt_kd: dq 0x00CF92000000FFFF
;;;;;;;;;;;;;;;;;;;;;;;;; GDT
iso_9660_directory_read:
    mov di, 0x7E01
    mov cx, 0xFFFF
    repne scasw
    sub di, 2
    push dx
    mov ax, [di-23]
    mov dx, [di-21]
    mov cx, 2048
    div cx
    cmp dx, 0
    pop dx
    jz .iso_9660_directory_configure_load
    inc ax
.iso_9660_directory_configure_load:
    mov cx, ax
    mov DWORD [si+4], 0x00008600
    push DWORD [si+4]
    mov ebx, DWORD [edi-31]
    mov DWORD [si+8], ebx
.iso_9660_directory_load_chunked:
    call int13_extended_read_procedure
    add WORD [si+6], 0x80
    add DWORD [si+8], 1
    loop .iso_9660_directory_load_chunked
    pop DWORD [si+4]
    ret
pvd_disk_address_packet:
    db 0x10               ; Size
    db 0x00               ; Reserved
    dw 0x0001             ; Sector Count
    dw 0x0000             ; Transfer Offset
    dw 0x07E0             ; Transfer Segment
    dq 0x0000000000000010 ; Disk Block Number
;;; INTERRUPT 10h, ECHO TEXTMODE PROCEDURE ;;
int10_echo_tm_procedure:
    mov ah, 0x0E
    int 0x10
    ret
;;; INTERRUPT 10h, ECHO TEXTMODE STRING PROCEDURE ;;
int10_echo_tm_str_procedure:
    mov ah, 0x0E
int10_echo_tm:
    lodsb
    cmp al, 0
    jz int10_echo_tm_procedure_end
    int 0x10
    jmp int10_echo_tm
int10_echo_tm_procedure_end:
    ret
;;; INTERRUPT 13h, EXTENDED READ ;;
int13_extended_read_procedure:
    mov ah, 0x42
    int 0x13
    jnc read_ok
    mov si, int10_msg_read_fail
    movzx bp, ah
    jmp print_error
read_ok:ret
;;; INTERRUPT 13h, EXTENSION CHECK OR RESET PROCEDURE ;;
int13_extensions_check_procedure:
    mov ah, 0x41
    mov bx, 0x55AA
    int 0x13
    jc int13_no_extension
    ret
int13_no_extension:
    mov si, int13_msg_no_extension
    jmp print_error
;;; AWAIT USER INPUT TO RESET PROCEDURE ;;
key_to_reset:
    call int16_get_key_procedure
    jmp int19_reset_procedure
;;; GET USER INPUT PROCEDURE ;;
int16_get_key_procedure:
    mov ah, 0x00
    int 0x16
    ret
;;; SYSTEM RESET PROCEDURE ;;
int19_reset_procedure:
    int 0x19
;;; PRINT ERROR PROCEDURE ;;
print_error:
    call int10_echo_tm_str_procedure
    jmp key_to_reset

int10_msg_kernel      : db "kernel ", 0
int10_msg_kernel_64   : db "not 32-bit", 0x0A, 0
int10_msg_kernel_86   : db "not x86", 0x0A, 0
int10_msg_read_fail   : db "cdrom read failure", 0x0A, 0
int13_msg_no_extension: db "unsupported bios", 0x0A, 0