#include <linux/module.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

MODULE_INFO(vermagic, VERMAGIC_STRING);

struct module __this_module
__attribute__((section(".gnu.linkonce.this_module"))) = {
 .name = KBUILD_MODNAME,
 .init = init_module,
#ifdef CONFIG_MODULE_UNLOAD
 .exit = cleanup_module,
#endif
 .arch = MODULE_ARCH_INIT,
};

static const struct modversion_info ____versions[]
__used
__attribute__((section("__versions"))) = {
	{ 0xbf073a9e, "module_layout" },
	{ 0x72542c85, "__wake_up" },
	{ 0x43b0c9c3, "preempt_schedule" },
	{ 0x859c6dc7, "request_threaded_irq" },
	{ 0x9d669763, "memcpy" },
	{ 0x9b388444, "get_zeroed_page" },
	{ 0xef6febb0, "kmalloc_caches" },
	{ 0x4aabc7c4, "__tracepoint_kmalloc" },
	{ 0x9fb1b2ac, "kmem_cache_alloc_notrace" },
	{ 0xf20dabd8, "free_irq" },
	{ 0x11f447ce, "__gpio_to_irq" },
	{ 0x37a0cba, "kfree" },
	{ 0x4302d0eb, "free_pages" },
	{ 0x3a8ad4dc, "interruptible_sleep_on" },
	{ 0x89e96c2, "__register_chrdev" },
	{ 0x6bc3fbc0, "__unregister_chrdev" },
	{ 0x76c6f7a2, "mem_section" },
	{ 0xea147363, "printk" },
	{ 0xefd6cf06, "__aeabi_unwind_cpp_pr0" },
};

static const char __module_depends[]
__used
__attribute__((section(".modinfo"))) =
"depends=";

