import { useState, useEffect } from 'react';
import { healthService } from '../services';

export function useHealthCheck() {
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
