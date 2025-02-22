import { DayInfo } from './WeekStatisticsPage.types';

export const getStartOfWeek = (dateStr: string) => {
  const date = new Date(dateStr);
  const day = date.getDay();
  const diff = date.getDate() - day + (day == 0 ? -6 : 1);
  date.setDate(diff);

  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const dayOfMonth = date.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${dayOfMonth}`;
};

export const isTodayInWeek = (weekStartDate: string) => {
  const startOfWeek = new Date(weekStartDate);
  const today = new Date();

  const endOfWeek = new Date(startOfWeek);
  endOfWeek.setDate(startOfWeek.getDate() + 6);

  return today >= startOfWeek && today <= endOfWeek;
};

export const isToday = (day: DayInfo) => {
  const today = new Date();

  return (
    day.year === today.getFullYear() &&
    day.month === today.getMonth() + 1 &&
    day.day === today.getDate()
  );
};

export const getWeekDateRange = (weekDays: DayInfo[]) => {
  if (weekDays.length === 0) return '';

  const start = weekDays[0];
  const end = weekDays[6];

  return `${start.month}월 ${start.day}일 (월) - ${end.month}월 ${end.day}일 (일)`;
};

export const isCreatedWeek = (weekStartDate: string, memberCreatedDate: string) => {
  const startOfWeek = new Date(weekStartDate);
  const createdDate = new Date(memberCreatedDate);

  const endOfWeek = new Date(startOfWeek);
  endOfWeek.setDate(startOfWeek.getDate() + 6);

  return createdDate >= startOfWeek && createdDate <= endOfWeek;
};
