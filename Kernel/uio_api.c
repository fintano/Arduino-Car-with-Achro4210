#include <linux/module.h>
#include <linux/slab.h>
#include <linux/device.h>
#include <linux/uio_driver.h>

static struct uio_info *info;
static struct device *dev;
static int irq = 1;
module_param(irq, int, S_IRUGO);

static void my_release(struct device *dev)
{
		printk(KERN_INFO "releasing my uio device\n");
}
static irqreturn_t my_handler(int irq, struct uio_info *dev_info)
{
		static int count = 0;
		printk(KERN_INFO "In UIO handler, count=%d\n", ++count);
		return IRQ_HANDLED;
}
static int my_init(void)
{
		dev = kzalloc(sizeof(struct device), GFP_KERNEL);
		dev_set_name(dev, "my_uio_device");
		dev->release = my_release;
		device_register(dev);
		info = kzalloc(sizeof(struct uio_info), GFP_KERNEL);
		info->name = "my_uio_device";
		info->version = "0.0.1";
		info->irq = irq;
		info->irq_flags = IRQF_SHARED;
		info->handler = my_handler;
		if (uio_register_device(dev, info) < 0) {
				device_unregister(dev);
				kfree(dev);
				kfree(info);
				printk(KERN_INFO "Failing to register uio device\n");
				return -1;
		}
		printk(KERN_INFO "Registered UIO handler for IRQ=%d\n", irq);
		return 0;
}
static void my_exit(void)
{
		uio_unregister_device(info);
		device_unregister(dev);
		printk(KERN_INFO "Un-Registered UIO handler for IRQ=%d\n", irq);
		kfree(info);
		kfree(dev);
}

module_init(my_init);
module_exit(my_exit);
//MODULE_AUTHOR("Jerry Cooperstein");
//MODULE_DESCRIPTION("LF331:1.6 s_18/lab8_uio_api.c");
MODULE_LICENSE("GPL v2");
