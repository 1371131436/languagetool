/* LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.ca;

import org.languagetool.rules.AbstractDateCheckFilter;

import java.util.Calendar;
import java.util.Locale;

/**
 * English localization of {@link AbstractDateCheckFilter}.
 * @since 2.7
 */
public class DateCheckFilter extends AbstractDateCheckFilter {

  @Override
  protected Calendar getCalendar() {
    return Calendar.getInstance(Locale.UK);
  }

  @SuppressWarnings("ControlFlowStatementWithoutBraces")
  @Override
  protected int getDayOfWeek(String dayStr) {
    String day = dayStr.toLowerCase();
    if (day.equalsIgnoreCase("dg") || day.equalsIgnoreCase("diumenge")) return Calendar.SUNDAY;
    if (day.equalsIgnoreCase("dl") || day.equalsIgnoreCase("dilluns")) return Calendar.MONDAY;
    if (day.equalsIgnoreCase("dt") || day.equalsIgnoreCase("dimarts")) return Calendar.TUESDAY;
    if (day.equalsIgnoreCase("dc") || day.equalsIgnoreCase("dimecres")) return Calendar.WEDNESDAY;
    if (day.equalsIgnoreCase("dj") || day.equalsIgnoreCase("dijous")) return Calendar.THURSDAY;
    if (day.equalsIgnoreCase("dv") || day.equalsIgnoreCase("divendres")) return Calendar.FRIDAY;
    if (day.equalsIgnoreCase("ds") || day.equalsIgnoreCase("dissabte")) return Calendar.SATURDAY;
    throw new RuntimeException("Could not find day of week for '" + dayStr + "'");
  }

  @SuppressWarnings({"ControlFlowStatementWithoutBraces", "MagicNumber"})
  @Override
  protected int getMonth(String monthStr) {
    String mon = monthStr.toLowerCase();
    if (mon.startsWith("gen")) return 1;
    if (mon.startsWith("febr")) return 2;
    if (mon.startsWith("març")) return 3;
    if (mon.startsWith("abr")) return 4;
    if (mon.startsWith("maig")) return 5;
    if (mon.startsWith("juny")) return 6;
    if (mon.startsWith("jul")) return 7;
    if (mon.startsWith("ag")) return 8;
    if (mon.startsWith("set")) return 9;
    if (mon.startsWith("oct")) return 10;
    if (mon.startsWith("nov")) return 11;
    if (mon.startsWith("des")) return 12;
    throw new RuntimeException("Could not find month '" + monthStr + "'");
  }

}
