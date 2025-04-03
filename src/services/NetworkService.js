import { NativeModules } from 'react-native';

const { NetworkModule } = NativeModules;

/**
 * 原生网络请求服务
 */
class NetworkService {
  /**
   * 执行GET请求（无参数）
   * @param {string} url - 请求URL
   * @returns {Promise<Object>} - 返回包含data和statusCode的对象
   */
  static async get(url) {
    try {
      return await NetworkModule.getRequest(url);
    } catch (error) {
      console.error('GET请求错误:', error);
      throw error;
    }
  }

  /**
   * 执行带参数的GET请求
   * @param {string} url - 请求URL
   * @param {Object} params - 请求参数对象
   * @returns {Promise<Object>} - 返回包含data和statusCode的对象
   */
  static async getWithParams(url, params) {
    try {
      return await NetworkModule.getRequestWithParams(url, params);
    } catch (error) {
      console.error('带参数GET请求错误:', error);
      throw error;
    }
  }

  /**
   * 执行POST请求（无参数）
   * @param {string} url - 请求URL
   * @returns {Promise<Object>} - 返回包含data和statusCode的对象
   */
  static async post(url) {
    try {
      return await NetworkModule.postRequest(url);
    } catch (error) {
      console.error('POST请求错误:', error);
      throw error;
    }
  }

  /**
   * 执行带参数的POST请求
   * @param {string} url - 请求URL
   * @param {Object} params - 请求参数对象
   * @returns {Promise<Object>} - 返回包含data和statusCode的对象
   */
  static async postWithParams(url, params) {
    try {
      return await NetworkModule.postRequestWithParams(url, params);
    } catch (error) {
      console.error('带参数POST请求错误:', error);
      throw error;
    }
  }
}

export default NetworkService; 