import { useState, useEffect } from 'react';
import { healthService } from '../services';
import type { SystemStatus } from '../types';

export function useHealthCheck() {
  const [status, setStatus] = useState<SystemStatus | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const checkStatus = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await healthService.getHealthStatus();
      setStatus(res.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Cannot reach backend server');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    checkStatus();
  }, []);

  return { status, loading, error, refetch: checkStatus };
}
