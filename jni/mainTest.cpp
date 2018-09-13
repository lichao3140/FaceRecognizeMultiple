
#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <omp.h>
#include <pthread.h>
#include <unistd.h> // sleep 的头文件

timeval getSystemTime() {
	struct timeval begin;
	gettimeofday(&begin, NULL);
	return begin;
}

double msecond()
{
    struct timeval tv;
    gettimeofday(&tv, 0);
    return (tv.tv_sec * 1.0e3 + tv.tv_usec * 1.0e-3);
}

double run_test()
{
	double f=0.0f;
	float f1 = 0.5f;
	float f2 = 1.0f;
	for (int i = 0; i < 1024; i++)
	{
		f += f1 * f2 * i;
	}
	return f;
}

int main(int argc, char **argv)
{
	printf("main() \r\n");
	double tm = msecond();
	double ret = 0;
//    #pragma omp parallel for
    for (int k = 0; k < 1000000;k++)
    {
    	ret = run_test();
    }
    sleep(1);
    printf("ret:%f \r\n", ret);
    double tm1 = msecond();
    printf("EF_Compare() end time ms:%f \r\n" , (float)(tm1 - tm));
    return 0;
}
