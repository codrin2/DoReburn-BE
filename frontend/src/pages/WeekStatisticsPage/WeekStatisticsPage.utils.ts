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
