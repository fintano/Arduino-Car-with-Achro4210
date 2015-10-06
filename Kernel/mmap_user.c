#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>

#define PAGE_SIZE     4096

int main ( int argc, char **argv )
{
		int configfd;
		char * address = NULL;
		unsigned long n = 0;

		configfd = open("dev/inter", O_RDWR);
		if(configfd < 0)
		{
				perror("Open call failed");
				return -1;
		}

		address = mmap(NULL, PAGE_SIZE, PROT_READ|PROT_WRITE, MAP_SHARED, configfd, 0);
		
		if (address == MAP_FAILED)
		{
				perror("mmap operation failed");
				return -1;
		}

		
		while(1){	
			printf("interrupt message: %d\n", *address);
			write(configfd, &n, sizeof(unsigned long)); // current Thread sleep 
			//sleep(500);
		}
		
		/*
		printf("Initial message: %s\n", address);
		memcpy(address + 11 , "*user*", 6);
		printf("Changed message: %s\n", address);
		*/
		close(configfd);    
		return 0;
}
