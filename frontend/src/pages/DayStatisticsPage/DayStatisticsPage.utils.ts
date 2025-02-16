export const getFormattedDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}`;
};

export const changeDateByDirection = (date: string, direction: 'prev' | 'next'): string => {
  const newDate = new Date(date);
  newDate.setDate(newDate.getDate() + (direction === 'prev' ? -1 : 1));

  return getFormattedDate(newDate);
};
