export interface ApiResponse<T = unknown> {
  status: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface SystemStatus {
  service: string;
  status: string;
  timestamp: string;
}
