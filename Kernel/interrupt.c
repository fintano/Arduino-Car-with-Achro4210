#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/interrupt.h>
#include <asm/irq.h>
#include <mach/gpio.h>
//#include <mach/fpga.h>
#include <mach/regs-gpio.h>
#include <linux/platform_device.h>
#include <asm/gpio.h>
#include <plat/gpio-cfg.h>
#include <linux/wait.h>
#include <linux/fs.h>
#include <linux/init.h>
#include <asm/io.h>
#include <asm/uaccess.h>
#include <linux/ioport.h>
#include <linux/version.h>
#include <linux/debugfs.h>
#include <linux/slab.h>
#include <linux/mm.h>  

#ifndef VM_RESERVED
# define  VM_RESERVED   (VM_DONTEXPAND | VM_DONTDUMP)
#endif


#define DEV_MAJOR 246
#define DEV_NAME "inter"

wait_queue_head_t wq_write;
DECLARE_WAIT_QUEUE_HEAD(wq_write);
int inter_open(struct inode *, struct file *);
int inter_release(struct inode *, struct file *);
ssize_t inter_write(struct file *, const unsigned long *, size_t, loff_t *);
int op_mmap(struct file *filp, struct vm_area_struct *vma);
irqreturn_t inter_handler(int irq, void* dev_id, struct pt_regs* reg);

static int inter_usage=0;
int interruptCount=0;
static struct file_operations inter_fops =
{
	.open = inter_open,
	.write = inter_write,
	.release = inter_release,
	.mmap = op_mmap,
};

struct dentry  *file;
struct mmap_info
{
		char *data;            
		int reference;      
};

struct mmap_info *info;

void mmap_open(struct vm_area_struct *vma)
{
		struct mmap_info *info = (struct mmap_info *)vma->vm_private_data;
		info->reference++;
}

void mmap_close(struct vm_area_struct *vma)
{
		struct mmap_info *info = (struct mmap_info *)vma->vm_private_data;
		info->reference--;
}

static int mmap_fault(struct vm_area_struct *vma, struct vm_fault *vmf)
{
		struct page *page;
		struct mmap_info *info;    

		info = (struct mmap_info *)vma->vm_private_data;
		if (!info->data)
		{
				printk("No data\n");
				return 0;    
		}

		page = virt_to_page(info->data);    

		get_page(page);
		vmf->page = page;            

		return 0;
}

struct vm_operations_struct mmap_vm_ops =
{
		.open =     mmap_open,
		.close =    mmap_close,
		.fault =    mmap_fault,    
};

int op_mmap(struct file *filp, struct vm_area_struct *vma)
{
		vma->vm_ops = &mmap_vm_ops;
		vma->vm_flags |= VM_RESERVED;    
		vma->vm_private_data = filp->private_data;
		mmap_open(vma);
		return 0;
}


irqreturn_t inter_handler1(int irq, void* dev_id, struct pt_regs* reg){

	int n = 'a';	
	//printk("interrupt1!!! = %x\n", gpio_get_value(S5PV310_GPX2(0)));
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler2(int irq, void* dev_id, struct pt_regs* reg){
	
	int n = 2;
	//printk("interrupt2!!! = %x\n", gpio_get_value(S5PV310_GPX2(1)));
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler3(int irq, void* dev_id, struct pt_regs* reg){
	int n = 3;
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler4(int irq, void* dev_id, struct pt_regs* reg){
	int n = 4;
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler5(int irq, void* dev_id, struct pt_regs* reg){
	int n = 'c';
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler6(int irq, void* dev_id, struct pt_regs* reg){
	int n = 'b';
	memcpy(info->data, &n, sizeof(int));
	wake_up_interruptible(&wq_write);
	return IRQ_HANDLED;
}

irqreturn_t inter_handler7(int irq, void* dev_id, struct pt_regs* reg){
	printk("interrupt7!!! = %x\n", 7);
	return IRQ_HANDLED;
}

int inter_open(struct inode *minode, struct file *mfile){

	int ret;
	info = kmalloc(sizeof(struct mmap_info), GFP_KERNEL);

	info->data = (char *)get_zeroed_page(GFP_KERNEL);
	memcpy(info->data, "hello from kernel this is file: ", 32);

	if(inter_usage!=0)
			return -EBUSY;
	inter_usage=1;

	mfile->private_data = info;

	/*
	*	SW2 : GPX2(0)
	*	SW3 : GPX2(1)
	*	SW4 : GPX2(2)
	*	SW6 : GPX2(4)
	*
	*	VOL+ : GPX2(5)
	*	POWER : GPX0(1)	
	*	VOL- : GPX2(3)
	*
	*	SW2,3,4,6 : PRESS-FALLING-0 / RELEASE-RISING-1
	*	VOL -, +  : PRESS-RISING-1 / RELEASE-FALLING-0		
	*	POWER	  : PRESS-FALLING-0 / RELEASE-RISING-1
	*/
	//printk(gpio_to_irq(S5PV310_GPX2(0)));	
/*	
	ret=request_irq(243, &inter_handler8, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(244, &inter_handler9, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(245, &inter_handler10, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(246, &inter_handler11, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(247, &inter_handler12, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(248, &inter_handler13, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(249, &inter_handler14, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(2509, &inter_handler15, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(218, &inter_handler16, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(219, &inter_handler17, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(220, &inter_handler18, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(221, &inter_handler19, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(222, &inter_handler20, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(223, &inter_handler21, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(224, &inter_handler22, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(225, &inter_handler23, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(227, &inter_handler24, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(226, &inter_handler25, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(228, &inter_handler26, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(229, &inter_handler27, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(230, &inter_handler28, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(231, &inter_handler29, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(232, &inter_handler30, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(233, &inter_handler31, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(234, &inter_handler32, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(235, &inter_handler33, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(236, &inter_handler34, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(237, &inter_handler35, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(238, &inter_handler36, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(239, &inter_handler37, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(240, &inter_handler38, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(241, &inter_handler39, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	ret=request_irq(242, &inter_handler40, IRQF_TRIGGER_RISING|IRQF_TRIGGER_FALLING, "X2.0", NULL);//SW2
	//printk(gpio_to_irq(S5PV310_GPX2(1)));	
*/	
	
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(0)), &inter_handler1, IRQF_TRIGGER_RISING, "X2.0", NULL);//SW2
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(1)), &inter_handler2, IRQF_TRIGGER_RISING, "X2.1", NULL);//SW3
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(2)), &inter_handler3, IRQF_TRIGGER_FALLING, "X2.2", NULL);//SW4
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(3)), &inter_handler5, IRQF_TRIGGER_RISING,"X2.3", NULL);//VOL-
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(4)), &inter_handler4, IRQF_TRIGGER_FALLING, "X2.4", NULL);//SW6
	ret=request_irq(gpio_to_irq(S5PV310_GPX2(5)), &inter_handler6, IRQF_TRIGGER_RISING, "X2.5", NULL);//VOL+
	ret=request_irq(gpio_to_irq(S5PV310_GPX0(1)), &inter_handler7, IRQF_TRIGGER_RISING, "X0.1", NULL);//POWER
	
	return 0;
}

int inter_release(struct inode *minode, struct file *mfile){
	
	//struct mmap_info *info = mfile->private_data;

	free_page((unsigned long)info->data);
	kfree(info);
	mfile->private_data = NULL;

	inter_usage=0;
	
	free_irq(gpio_to_irq(S5PV310_GPX2(0)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX2(1)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX2(2)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX2(3)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX2(4)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX2(5)), NULL);
	free_irq(gpio_to_irq(S5PV310_GPX0(1)), NULL);

	return 0;
}

ssize_t inter_write(struct file *inode, const unsigned long *gdata, size_t length, loff_t *off_what){
	
	//if(interruptCount==0){
		printk("sleep on\n");
		interruptible_sleep_on(&wq_write);
	//}
	printk("write\n");

	return length;
}

int __init inter_init(void){
		int result;
		result = register_chrdev(DEV_MAJOR,DEV_NAME, &inter_fops);
		interruptCount=0;

		if(result <0) {
			printk(KERN_WARNING"Can't get any major!\n");
			return result;
		}

		return 0;
}

void __exit inter_exit(void){
		unregister_chrdev(DEV_MAJOR,DEV_NAME);
}

module_init(inter_init);
module_exit(inter_exit);

MODULE_LICENSE("GPL");
