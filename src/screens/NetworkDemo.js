import React, { useState } from 'react';
import {
  StyleSheet,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
  SafeAreaView,
} from 'react-native';
import NetworkService from '../services/NetworkService';

const NetworkDemo = () => {
  const [url, setUrl] = useState('https://jsonplaceholder.typicode.com/posts/1');
  const [params, setParams] = useState('{"userId": 1}');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState('');

  // 处理GET请求（无参数）
  const handleGetRequest = async () => {
    if (!url) {
      setResult('请输入有效的URL');
      return;
    }

    try {
      setLoading(true);
      const response = await NetworkService.get(url);
      setResult(JSON.stringify(response, null, 2));
    } catch (error) {
      setResult(`错误: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // 处理带参数的GET请求
  const handleGetWithParamsRequest = async () => {
    if (!url) {
      setResult('请输入有效的URL');
      return;
    }

    try {
      setLoading(true);
      const paramsObj = params ? JSON.parse(params) : {};
      const response = await NetworkService.getWithParams(url, paramsObj);
      setResult(JSON.stringify(response, null, 2));
    } catch (error) {
      setResult(`错误: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // 处理POST请求（无参数）
  const handlePostRequest = async () => {
    if (!url) {
      setResult('请输入有效的URL');
      return;
    }

    try {
      setLoading(true);
      const response = await NetworkService.post(url);
      setResult(JSON.stringify(response, null, 2));
    } catch (error) {
      setResult(`错误: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // 处理带参数的POST请求
  const handlePostWithParamsRequest = async () => {
    if (!url) {
      setResult('请输入有效的URL');
      return;
    }

    try {
      setLoading(true);
      const paramsObj = params ? JSON.parse(params) : {};
      const response = await NetworkService.postWithParams(url, paramsObj);
      setResult(JSON.stringify(response, null, 2));
    } catch (error) {
      setResult(`错误: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.contentContainer}>
        <Text style={styles.title}>原生网络请求演示</Text>
        
        <Text style={styles.label}>URL:</Text>
        <TextInput
          style={styles.input}
          value={url}
          onChangeText={setUrl}
          placeholder="输入请求URL"
        />
        
        <Text style={styles.label}>参数 (JSON格式):</Text>
        <TextInput
          style={[styles.input, styles.paramsInput]}
          value={params}
          onChangeText={setParams}
          placeholder="输入请求参数 (JSON格式)"
          multiline
        />
        
        <View style={styles.buttonContainer}>
          <TouchableOpacity
            style={styles.button}
            onPress={handleGetRequest}
            disabled={loading}
          >
            <Text style={styles.buttonText}>GET</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.button}
            onPress={handleGetWithParamsRequest}
            disabled={loading}
          >
            <Text style={styles.buttonText}>GET带参数</Text>
          </TouchableOpacity>
        </View>
        
        <View style={styles.buttonContainer}>
          <TouchableOpacity
            style={styles.button}
            onPress={handlePostRequest}
            disabled={loading}
          >
            <Text style={styles.buttonText}>POST</Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={styles.button}
            onPress={handlePostWithParamsRequest}
            disabled={loading}
          >
            <Text style={styles.buttonText}>POST带参数</Text>
          </TouchableOpacity>
        </View>
        
        {loading && (
          <ActivityIndicator size="large" style={styles.loader} />
        )}
        
        <Text style={styles.resultTitle}>响应结果:</Text>
        <ScrollView style={styles.resultContainer}>
          <Text style={styles.resultText}>{result}</Text>
        </ScrollView>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  contentContainer: {
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 10,
    marginBottom: 15,
    fontSize: 16,
  },
  paramsInput: {
    height: 100,
    textAlignVertical: 'top',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  button: {
    backgroundColor: '#007BFF',
    padding: 12,
    borderRadius: 8,
    flex: 0.48,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  loader: {
    marginVertical: 20,
  },
  resultTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginVertical: 10,
  },
  resultContainer: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 10,
    maxHeight: 200,
    marginBottom: 20,
  },
  resultText: {
    fontSize: 14,
    fontFamily: 'monospace',
  },
});

export default NetworkDemo; 