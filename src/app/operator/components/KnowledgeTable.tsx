"use client";

import { useEffect, useState } from "react";

interface KnowledgeMetadata {
  phonetic?: string;
  [key: string]: unknown;
}

interface Knowledge {
  code: string;
  name: string;
  description: string;
  metadata: KnowledgeMetadata;
  created_at: string;
  updated_at: string;
}

export function KnowledgeTable() {
  const [knowledges, setKnowledges] = useState<Knowledge[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchKnowledges = async () => {
      try {
        const res = await fetch("/api/knowledge");
        if (!res.ok) {
          if (res.status === 401 || res.status === 403) {
            setError("权限不足");
            return;
          }
          setError("加载失败");
          return;
        }
        const data = await res.json();
        setKnowledges(data);
      } catch (err) {
        setError("加载失败");
      } finally {
        setLoading(false);
      }
    };

    fetchKnowledges();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-gray-600 dark:text-gray-400">加载中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-red-600 dark:text-red-400">{error}</div>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead className="bg-gray-50 dark:bg-gray-900">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                代码
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                名称
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                描述
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                音标
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                创建时间
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                更新时间
              </th>
            </tr>
          </thead>
          <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            {knowledges.length === 0 ? (
              <tr>
                <td
                  colSpan={6}
                  className="px-6 py-4 text-center text-gray-500 dark:text-gray-400"
                >
                  暂无数据
                </td>
              </tr>
            ) : (
              knowledges.map((knowledge) => (
                <tr
                  key={knowledge.code}
                  className="hover:bg-gray-50 dark:hover:bg-gray-700"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900 dark:text-white">
                    {knowledge.code}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                    {knowledge.name}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-400 max-w-md truncate">
                    {knowledge.description}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {knowledge.metadata?.phonetic ? (
                      <div className="flex items-center gap-2">
                        <span>{knowledge.metadata.phonetic}</span>
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            if ("speechSynthesis" in window && knowledge.name) {
                              window.speechSynthesis.cancel();
                              const utter = new SpeechSynthesisUtterance(
                                knowledge.name
                              );
                              utter.lang = "en-US";
                              utter.rate = 0.8;
                              window.speechSynthesis.speak(utter);
                            }
                          }}
                          className="p-1.5 text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 dark:hover:text-indigo-300 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 rounded transition-colors"
                          aria-label="播放单词"
                          title="播放单词"
                        >
                          <svg
                            className="w-4 h-4"
                            fill="none"
                            strokeWidth={1.5}
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                            xmlns="http://www.w3.org/2000/svg"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="M19.114 5.636a9 9 0 0 1 0 12.728M16.463 8.288a5.25 5.25 0 0 1 0 7.424M6.75 8.25l4.72-4.72a.75.75 0 0 1 .53-.22H15a.75.75 0 0 1 .75.75v15a.75.75 0 0 1-.75.75h-3.25a.75.75 0 0 1-.53-.22l-4.72-4.72H4.5a.75.75 0 0 1-.75-.75V9a.75.75 0 0 1 .75-.75h2.25Z"
                            />
                          </svg>
                        </button>
                      </div>
                    ) : (
                      "-"
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {new Date(knowledge.created_at).toLocaleDateString("zh-CN")}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    {new Date(knowledge.updated_at).toLocaleDateString("zh-CN")}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

