/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.dozer.util.mapping.converters;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.beanutils.Converter;

/**
 * @author tierney.matt
 */
public class DateConverter implements Converter {
  private DateFormat dateFormat;

  public DateConverter(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  public Object convert(Class destClass, Object sourceObj) {
    Object result = null;

    Class sourceFieldClass = sourceObj.getClass();
    long time = -1;
    //Calendar to Date
    if (Calendar.class.isAssignableFrom(sourceFieldClass)) {
      Calendar inVal = (Calendar) sourceObj;
      time = inVal.getTime().getTime();
    //Date to Date
    } else if (java.util.Date.class.isAssignableFrom(sourceFieldClass)) {
        time = ( (java.util.Date) sourceObj).getTime();
    //String to Date
    } else if (dateFormat != null && String.class.isAssignableFrom(sourceObj.getClass())) {
        try {
          if("".equals(sourceObj)){
            return null;
          }
          time = dateFormat.parse( (String) sourceObj).getTime();
        } catch (ParseException e) {
          throw new ConversionException("Unable to parse source object using specified date format", e);
        }
    //Default conversion
    } else {
      try {
        time = Long.parseLong(sourceObj.toString());
      } catch (NumberFormatException e) {
        throw new ConversionException("Unable to determine time in millis of source object",e);
      }
    }

    try {
      Constructor constructor = destClass.getConstructor(new Class[] {Long.TYPE});
      result = constructor.newInstance(new Object[] {new Long(time)});
    } catch (Exception e) {
      throw new ConversionException(e);
    }

    return result;

  }

  public DateFormat getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }
}