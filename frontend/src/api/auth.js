import api from './axios';

// Register new user
export const registerApi = async (userData) => {
  try {
    const response = await api.post('/users/register', userData);
    return response.data;
  } catch (error) {
    console.error('Registration failed:', error);
    throw error;
  }
};

// Login user (simulated since backend doesn't have login endpoint)
export const loginApi = async (email, password) => {
  try {
    // Since backend doesn't have login endpoint yet, we'll simulate authentication
    // by checking if user exists and returning user data
    const response = await api.get('/users');
    const users = response.data;
    const user = users.find(u => u.email === email);
    
    if (!user) {
      throw new Error('User not found');
    }
    
    // For now, we'll simulate successful login
    // In production, you should implement proper authentication
    return {
      user: {
        id: user.id,
        username: user.username,
        email: user.email
      },
      token: "simulated-token-" + user.id
    };
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};

// Get current user info
export const meApi = async (token) => {
  try {
    if (!token) {
      throw new Error('No token available');
    }

    // Extract user ID from simulated token
    const userId = token.replace('simulated-token-', '');
    
    const response = await api.get(`/users/${userId}`);
    return response.data;
  } catch (error) {
    console.error('Failed to get user info:', error);
    throw error;
  }
};

// Logout user
export const logoutApi = async () => {
  try {
    // Since backend doesn't have logout endpoint, we'll just clear local storage
    // In production, you should implement proper logout on the backend
    console.log("Logout successful - clearing local storage");
  } catch (error) {
    console.error('Logout failed:', error);
    throw error;
  }
};
