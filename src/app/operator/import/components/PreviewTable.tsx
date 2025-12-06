"use client";

import { useState } from "react";
import { CSVData } from "../hooks/useCSVParser";
import { WordData } from "../types";
import { Paginator } from "./Paginator";

interface PreviewTableProps {
  data: CSVData;
}

const ITEMS_PER_PAGE = 10;

export function PreviewTable({ data }: PreviewTableProps) {
  const [currentPage, setCurrentPage] = useState(1);
  const totalRows = data.rows.length;
  const totalPages = Math.ceil(totalRows / ITEMS_PER_PAGE);

  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const currentRows: WordData[] = data.rows.slice(startIndex, endIndex);

  // 定义要显示的列
  const displayColumns = [
    { key: "name", label: "单词" },
    { key: "description", label: "翻译" },
    { key: "metadata.pos", label: "词性" },
    { key: "metadata.level", label: "等级" },
    { key: "metadata.example", label: "例句" },
  ];

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div className="mb-6 md:mb-8">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-900">
              <tr>
                {displayColumns.map((col, idx) => (
                  <th
                    key={idx}
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
                  >
                    {col.label}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {currentRows.map((row, rowIdx) => (
                <tr
                  key={startIndex + rowIdx}
                  className="hover:bg-gray-50 dark:hover:bg-gray-700"
                >
                  <td className="px-6 py-4 text-sm font-medium text-gray-900 dark:text-white">
                    {row.name || "-"}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900 dark:text-white">
                    {row.description || "-"}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-400">
                    {row.metadata?.pos || "-"}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-400">
                    {row.metadata?.level || "-"}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-400 max-w-md">
                    {row.metadata?.example || "-"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {totalPages > 1 && (
          <Paginator
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            itemsPerPage={ITEMS_PER_PAGE}
            totalItems={totalRows}
          />
        )}
      </div>
    </div>
  );
}

