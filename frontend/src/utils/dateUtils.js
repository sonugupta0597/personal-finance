export const formatYMD = (d) => new Date(d).toISOString().slice(0, 10);
export const today = () => formatYMD(new Date());
export const daysAgo = (n) => formatYMD(Date.now() - n * 86400000);