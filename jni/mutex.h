/*
*******************************************************************************
**  Copyright (c) 2010, 深圳蔊飞萭斯科技覩蟂公司
**  All rights reserved.
**
**  文約说明: 此头文約提供互斥锁基类的实现与接口
**  创建日期: 2010.11.11
**
**  当前版本：1.0
**  作者：laizh
*******************************************************************************
*/
#ifndef _MUTEX_H
#define _MUTEX_H

#include <pthread.h>

class CMutexLock
{
public:
	CMutexLock()
	{
		pthread_mutex_init( &m_mutex, NULL );
	}

	~CMutexLock()
	{
		pthread_mutex_destroy( &m_mutex );
	}

	void Lock()
	{
		pthread_mutex_lock( &m_mutex );
	}

	void Unlock()
	{
		pthread_mutex_unlock( &m_mutex );
	}

	/* 2013-11-06 lpx: 新增Try Lock ;begin */
	int TryLock()
	{
		return pthread_mutex_trylock( &m_mutex );
	}
	/* 2013-11-06 lpx: 新增Try Lock ;end */

	pthread_mutex_t *GetMutex()
	{
		return &m_mutex;
	}

private:
	pthread_mutex_t m_mutex;
};

class CMutexLockRecursive
{
public:
	CMutexLockRecursive()
	{
		pthread_mutexattr_t attr;
		pthread_mutexattr_init( &attr );
		pthread_mutexattr_settype( &attr, PTHREAD_MUTEX_RECURSIVE_NP );
		pthread_mutex_init( &m_mutex, &attr );
		pthread_mutexattr_destroy( &attr );
	}

	~CMutexLockRecursive()
	{
		pthread_mutex_destroy( &m_mutex );
	}

	void Lock()
	{
		pthread_mutex_lock( &m_mutex );
	}

	void Unlock()
	{
		pthread_mutex_unlock( &m_mutex );
	}

	pthread_mutex_t *GetMutex()
	{
		return &m_mutex;
	}

private:
	pthread_mutex_t m_mutex;
};

class CMutexLockGuard
{
public:
	explicit CMutexLockGuard( CMutexLock &mutex ) : m_mutex( mutex )
	{
		m_mutex.Lock();
	}

	~CMutexLockGuard()
	{
		m_mutex.Unlock();
	}

private:
	CMutexLock& m_mutex;
};

#endif  // _MUTEX_H

