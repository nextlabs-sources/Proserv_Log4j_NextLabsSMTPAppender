/*
 *
 *
============================================================================
 *     This license is based on the Apache Software License, Version 1.1
 *
============================================================================
 *
 *    Copyright (C) 2001-2003 Mark Masterson. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names Mark Masterson, M2 Technologies, SNMPTrapAppender or log4j must
 *    not be used to endorse or promote products derived  from this  software
 *    without  prior written permission. For written permission, please contact
 *    m.masterson@computer.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package com.nextlabs.appender;

/**
 * Title: SysUpTimeResolver<br>
 * Description: A class implementing this interface must provide a way of returning the system "up time",
 * in milliseconds. For example, you might have a class the uses a JNI call to an operating system
 * API to determine the real "up time" of the host machine.  You would specify the FQN of your
 * implmenting class in your properties file.  The SNMPTrapAppender includes a default implementation
 * of this interface that simply calls System.currentTimeMillis() to set the time.<br>
 * @author Thomas Muller (<a href="mailto:ttm@online.no">ttm@online.no</a>)
 * @author Mark Masterson (<a href="mailto:m.masterson@computer.org">m.masterson@computer.org</a>)<br>
 * <a href="http://www.m2technologies.net/">http://www.m2technologies.net/</a><br>
 * @version 1.0<br>
 */
public interface SysUpTimeResolver {
    /**
     * Implements a mechanism to return a "system up-time" that is relevant and meaningful
     * for a given application.
     * @return a milliseconds value as a long.
     */
    long getSysUpTime();
}
