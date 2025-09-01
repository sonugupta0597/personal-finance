import { useContext } from "react";
import { TransactionContext } from "../contexts/TransactionContext";
export default function useTransactions() { return useContext(TransactionContext); }