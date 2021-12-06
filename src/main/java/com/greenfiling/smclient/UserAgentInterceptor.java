/**
 * Copyright 2021 Green Filing, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greenfiling.smclient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link okhttp3.Interceptor} for setting a custom User-Agent
 * 
 * @author jetmore
 * @since 1.0.1
 */
public class UserAgentInterceptor implements Interceptor {
  public final String userAgent;

  public UserAgentInterceptor(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request userAgentRequest = chain.request().newBuilder().header("User-Agent", userAgent).build();
    return chain.proceed(userAgentRequest);
  }

}
